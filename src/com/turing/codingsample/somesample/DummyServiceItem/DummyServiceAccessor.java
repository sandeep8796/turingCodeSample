package com.turing.codingsample.somesample.DummyServiceItem;

//some of the imports are removed
import com.google.inject.Inject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DummyServiceAccessor implements DependentService<DummyServiceRequest, AsinAttributes> {
    private final DatapathClient datapathClient;
    private final MetricsClient metricsClient;
    private final DummyServiceRequestProcessor dummyServiceRequestProcessor;

    @Inject
    public DummyServiceAccessor(final DatapathClient datapathClient,
                                final MetricsClient metricsClient,
                                final DummyServiceRequestProcessor dummyServiceRequestProcessor) {
        this.datapathClient = datapathClient;
        this.metricsClient = metricsClient;
        this.dummyServiceRequestProcessor = dummyServiceRequestProcessor;
    }

    @Override
    public AsinAttributes call(@NonNull final DummyServiceRequest dummyServiceRequest) {
        try {
            final AsinAttributes asinAttributes = datapathClient.callWithRetry(dummyServiceRequest,
                    dummyServiceRequestProcessor,
                    metricsClient);
            return asinAttributes;
        } catch (final IllegalStateException e) {
            final String errMessage = String.format("DummyService.GetItem returns Empty Response for ASIN: %s and marketplaceId %d",
                    dummyServiceRequest.getAsin(),
                    dummyServiceRequest.getMarketplaceId());
            throw new IllegalStateException(errMessage, e);
        } catch (final Exception e) {
            final String errMessage = String.format("DummyService.GetItem calls failed for ASIN: %s and marketplaceId %d",
                    dummyServiceRequest.getAsin(),
                    dummyServiceRequest.getMarketplaceId());
            throw new DependentServiceFailureException(errMessage, e);
        }
    }
}
