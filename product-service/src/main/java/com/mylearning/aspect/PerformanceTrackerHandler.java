package com.mylearning.aspect;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.extern.slf4j.Slf4j;

/*
    this HandlerClass was written to track the method execution
    now register this HandlerClass with the ObservationRegistry
 */
@Slf4j
public class PerformanceTrackerHandler
        //implements ObservationHandler<Observation.Context>
                                                                {

    /*@Override
    public void onStart(Observation.Context context) {
        log.info("execution started {}", context.getName());
        //ObservationHandler.super.onStart(context);
        context.put("time", System.currentTimeMillis());
    }

    @Override
    public void onError(Observation.Context context) {
        //ObservationHandler.super.onError(context);
        log.info("Error occurred {} ", context.getError().getMessage());
    }

    @Override
    public void onEvent(Observation.Event event, Observation.Context context) {
        ObservationHandler.super.onEvent(event, context);
    }

    @Override
    public void onScopeOpened(Observation.Context context) {
        ObservationHandler.super.onScopeOpened(context);
    }

    @Override
    public void onScopeClosed(Observation.Context context) {
        ObservationHandler.super.onScopeClosed(context);
    }

    @Override
    public void onScopeReset(Observation.Context context) {
        ObservationHandler.super.onScopeReset(context);
    }

    @Override
    public void onStop(Observation.Context context) {
        //ObservationHandler.super.onStop(context);
        log.info(
                "execution stopped "
                        + context.getName()
                        + " duration "
                        + (System.currentTimeMillis() - context.getOrDefault("time", 0L)));
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }*/
}
