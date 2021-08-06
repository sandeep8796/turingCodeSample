package com.turing.codingsample.somesample.datapath;

//some of the imports are removed
import java.io.IOException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import software.amazon.cloudwatchlogs.emf.model.Unit;

/**
 * This class build the datapath client and have methods to call the DummyService(datapath gateway).
 */
@Slf4j
public class DatapathClient {
    private static final String INVOCATION_COUNT_METRIC = "DummyService.getCatalogItem.Invocations";
    private static final String FAILURE_COUNT_METRIC = "DummyService.getCatalogItem.Failures";
    private static final String LATENCY_METRIC = "DummyService.getCatalogItem.Latency";

    private final String authorizedToken;
    private final RetryStrategy retryStrategy;
    private final RetryContext retryContext;
    private final HttpClient httpClient;

    public DatapathClient(final String authorizedToken,
                          final RetryStrategy retryStrategy,
                          final RetryContext retryContext,
                          final HttpClient httpClient) {

        this.authorizedToken = authorizedToken;
        this.retryStrategy = retryStrategy;
        this.retryContext = retryContext;
        this.httpClient = httpClient;
    }

    /**
     * This method should be used when there is a need of retrying the call to catalog V3
     *
     * @param request The input to call the DummyService api
     * @param requestProcessor object to parse the response and build the output
     * @param metricsClient To record the metrics
     * @return
     */
    public AsinAttributes callWithRetry(@NonNull final CatalogItemRequest request,
                               @NonNull final DatapathRequestProcessor requestProcessor,
                               final MetricsClient metricsClient) throws Exception {

        try {
            return new RetryingCallable<RetryContext, AsinAttributes>(
                    retryStrategy,
                    retryContext,
                    () -> call(request, requestProcessor, metricsClient)).call();
        } catch (final Exception e) {
            final String errorMessage = String.format("Failed to call Datapath on request: %s", request);
            log.error(errorMessage);
            throw e;
        }
    }

    /**
     * This method should be used when there is no need of retrying the call to catalog V3
     *
     * @param request The input to call the DummyService api
     * @param requestProcessor object to parse the response and build the output
     * @param metricsClient To record the metrics
     * @return
     */
    public AsinAttributes call(@NonNull final CatalogItemRequest request,
                               @NonNull final DatapathRequestProcessor requestProcessor,
                               final MetricsClient metricsClient) {
        metricsClient.countOne(INVOCATION_COUNT_METRIC);
        final String requestUrl = requestProcessor.buildRequestUrl(request);
        final HttpGet get = new HttpGet(requestUrl);

        get.setHeader(HEADER_CLIENT_ID, this.authorizedToken);
        get.setHeader(HEADER_SERVICE, AAA_SERVICE_NAME);
        get.setHeader(HEADER_OPERATION, HEADER_OPERATION);

        boolean failure = false;
        final long startTime = System.nanoTime();
        try {
            final HttpResponse response = httpClient.execute(get);
            final StatusLine statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
                final String errorMessage = String.format("HTTP Status code is not OK, code: %d, request: %s",
                        statusLine.getStatusCode(),
                        request);
                log.info(errorMessage);
                failure = true;
                final InvalidHttpResponseCode invalidHttpResponseCode = new InvalidHttpResponseCode(errorMessage);
                throw new DependentServiceFailureException(errorMessage, invalidHttpResponseCode.getCause());
            }

            final HttpEntity entity = response.getEntity();
            if (entity == null) {
                failure = true;
                final String errorMessage = String.format("Missing data in catalog for request: %s", request);
                log.error(errorMessage);
                throw new IllegalStateException(errorMessage);
            }

            try {
                return requestProcessor.buildOutput(request, entity);
            } catch (final MalformedDataException e) {
                failure = true;
                final String errorMessage = String.format("There was a problem parsing the response for request: %s", request);
                throw new DependentServiceFailureException(errorMessage, e);
            }
        } catch (final IOException e) {
            final String errorMessage = String.format("IOException when calling Datapath for request: %s", request);
            log.error(errorMessage);
            failure = true;
            throw new DependentServiceFailureException(errorMessage, e);
        } finally {
            if (failure) {
                metricsClient.countOne(FAILURE_COUNT_METRIC);
            }
            metricsClient.publish(LATENCY_METRIC, System.nanoTime() - startTime, Unit.SECONDS);
        }
    }
}