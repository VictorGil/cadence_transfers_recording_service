package net.devaction.cadence.transfersrecordingservice.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class ConfigValues {

    @JsonProperty("kafka_bootstrap_servers")
    private String kafkaBootstrapServers;

    @JsonProperty("kafka_schema_registry_URL")
    private String kafkaSchemaRegistryUrl;

    @JsonProperty("kafka_transfers_topic")
    private String kafkaTransfersTopic;

    @JsonProperty("kafka_polling_millis")
    private int kafkaPollingMillis;

    @JsonProperty("cadence_domain")
    private String cadenceDomain;

    @JsonProperty("cadence_workers")
    private int cadenceWorkers;

    @Override
    public String toString() {
        return "ConfigValues [kafkaBootstrapServers=" + kafkaBootstrapServers
                + ", kafkaSchemaRegistryUrl=" + kafkaSchemaRegistryUrl
                + ", kafkaTransfersTopic=" + kafkaTransfersTopic
                + ", kafkaPollingMillis=" + kafkaPollingMillis

                + ", cadenceDomain=" + cadenceDomain
                + ", cadenceWorkers=" + cadenceWorkers
                + "]";
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String bootstrapServers) {
        this.kafkaBootstrapServers = bootstrapServers;
    }

    public String getKafkaSchemaRegistryUrl() {
        return kafkaSchemaRegistryUrl;
    }

    public void setKafkaSchemaRegistryUrl(String kafkaSchemaRegistryUrl) {
        this.kafkaSchemaRegistryUrl = kafkaSchemaRegistryUrl;
    }

    public String getCadenceDomain() {
        return cadenceDomain;
    }

    public void setCadenceDomain(String cadenceDomain) {
        this.cadenceDomain = cadenceDomain;
    }

    public int getCadenceWorkers() {
        return cadenceWorkers;
    }

    public void setCadenceWorkers(int cadenceWorkers) {
        this.cadenceWorkers = cadenceWorkers;
    }

    public String getKafkaTransfersTopic() {
        return kafkaTransfersTopic;
    }

    public void setKafkaTransfersTopic(String kafkaTransfersTopic) {
        this.kafkaTransfersTopic = kafkaTransfersTopic;
    }

    public int getKafkaPollingMillis(){
        return kafkaPollingMillis;
    }

    public void setKafkaPollingMillis(int kafkaPollingMillis){
        this.kafkaPollingMillis = kafkaPollingMillis;
    }
}
