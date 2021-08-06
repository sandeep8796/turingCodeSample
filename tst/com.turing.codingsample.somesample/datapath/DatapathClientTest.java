package com.turing.codingsample.somesample.datapath;

//some of the imports are removed
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DatapathClientTest {
    private static final String ASIN = "Test123";
    private static final long MARKETPLACE_ID = 1L;
    private static final String BINDING = "electronics";
    private static final String CATEGORY = "testCategory";
    private static final String SUB_CATEGORY = "testSubCategory";
    private static final String CATALOG_CLIENT = "CatalogClient.GetItem";
    private static final String METRIC_NAME = "AdaptiveRetryStrategyMetricsRecorder" + "." + CATALOG_CLIENT;
    private static final Collection<Class<? extends Throwable>> recoverableExceptions = new ArrayList<>(Calls.getCommonRecoverableThrowables());
    private static final RetryStrategy RETRY_STRATEGY =  new AdaptiveRetryStrategyBuilder()
            .retryOn(recoverableExceptions)
            .withRetryDelayMillis(100)
            .build();

    @Mock private DatapathRequestProcessor datapathRequestProcessor;
    @Mock private MetricsClient metrics;
    @Mock private HttpClient httpClient;
    @Mock private HttpResponse response;
    @Mock private StatusLine statusLine;
    @Mock private HttpEntity httpEntity;

    private final AdaptiveRetryStrategyMetricsRecorder metricsRecorder = new AdaptiveRetryStrategyMetricsRecorder(
            metrics,
            METRIC_NAME);
    private final RetryContext RETRY_CONTEXT = new RetryContext(CATALOG_CLIENT,
            metricsRecorder);
    private DatapathClient subject;
    private CatalogItemRequest catalogItemRequest;

    @BeforeEach
    void setup() {
        when(datapathRequestProcessor.buildRequestUrl(any())).thenReturn("https://test.com/catalog/item");
        subject = new DatapathClient("auth_token",
                RETRY_STRATEGY,
                RETRY_CONTEXT,
                httpClient);
        catalogItemRequest = CatalogItemRequest.builder()
                .asin(ASIN)
                .marketplaceId(MARKETPLACE_ID)
                .build();
    }

    @Test
    void test_CallWithRetry_OnSuccess_ReturnAsinDetails() throws Exception {
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(response);
        when(response.getEntity()).thenReturn(httpEntity);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(datapathRequestProcessor.buildOutput(any(), any())).thenReturn(buildExpectedAsinDetails());

        AsinAttributes asinDetails = subject.callWithRetry(catalogItemRequest,
                datapathRequestProcessor,
                metrics);

        Assertions.assertEquals(asinDetails.getBinding(), BINDING);
        Assertions.assertEquals(asinDetails.getProductCategory(), CATEGORY);
        Assertions.assertEquals(asinDetails.getProductSubcategory(), SUB_CATEGORY);
    }

    @Test
    void test_CallWithRetry_Given_NullHttpEntity_ThrowsIllegalStateException() throws Exception {
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(response.getEntity()).thenReturn(null);
        when(datapathRequestProcessor.buildOutput(any(), any())).thenReturn(buildExpectedAsinDetails());

        Assertions.assertThrows(IllegalStateException.class,
                () -> subject.callWithRetry(catalogItemRequest,
                datapathRequestProcessor,
                metrics));
    }

    @Test
    void testCallWithRetry_OnFailure_ThrowDependentServiceFailureException() throws IOException {
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_REQUEST_TIMEOUT);

        Assertions.assertThrows(DependentServiceFailureException.class, () -> subject.callWithRetry(catalogItemRequest,
                datapathRequestProcessor,
                metrics));
    }

    @Test
    void testCallWithRetry_On_RequestProcessorException_OnFailure_DependentServiceFailureException() throws IOException {
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
        when(response.getEntity()).thenReturn(httpEntity);
        when(datapathRequestProcessor.buildOutput(any(), any())).thenThrow(new MalformedDataException("error"));

        Assertions.assertThrows(DependentServiceFailureException.class, () -> subject.callWithRetry(catalogItemRequest,
                datapathRequestProcessor,
                metrics));
    }

    @Test
    void testCallWithRetry_On_IoException_ThrowDependentServiceFailureException() throws IOException {
        when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(new SSLException("failed to connect"));

        Assertions.assertThrows(DependentServiceFailureException.class, () -> subject.callWithRetry(catalogItemRequest,
                datapathRequestProcessor,
                metrics));
    }

    @Test
    void testCallWithRetry_Given_NullRequest_ThrowsIllegalArgumentException() throws IOException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> subject.callWithRetry(null,
                datapathRequestProcessor,
                metrics));
    }

    @Test
    void testCallWithRetry_Given_NullProcessor_ThrowsIllegalArgumentException() throws IOException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> subject.callWithRetry(catalogItemRequest,
                null,
                metrics));
    }

    @Test
    void testCall_Given_NullRequest_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> subject.call(null,
                datapathRequestProcessor,
                metrics));
    }

    @Test
    void testCall_Given_NullProcessor_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> subject.call(catalogItemRequest,
                null,
                metrics));
    }

    private AsinAttributes buildExpectedAsinDetails() {
        return AsinAttributes.builder()
                .binding(BINDING)
                .productCategory(CATEGORY)
                .productSubcategory(SUB_CATEGORY)
                .build();
    }
}
