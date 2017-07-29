package in.ashwanthkumar.manthan.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class Service extends Application<ServiceConfiguration> {
    public static void main(String[] args) throws Exception {
        new Service().run(args);
    }

    @Override
    public void run(ServiceConfiguration serviceConfiguration, Environment environment) throws Exception {
        environment.jersey().register(new ReportResource("aggregate_1.5M.jsonl"));
    }
}
