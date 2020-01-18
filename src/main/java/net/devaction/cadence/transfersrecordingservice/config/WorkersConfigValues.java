package net.devaction.cadence.transfersrecordingservice.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class WorkersConfigValues {

    @JsonProperty("cadence_domain")
    private String cadenceDomain;

    @JsonProperty("cadence_workers")
    private int cadenceWorkers;

    @JsonProperty("kafka_bootstrap_servers")
    private String kafkaBootstrapServers;

    @JsonProperty("kafka_schema_registry_URL")
    private String kafkaSchemaRegistryUrl;

    @JsonProperty("kafka_account_balances_topic")
    private String kafkaAccountBalancesTopic;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " ["
                + "cadenceDomain=" + cadenceDomain
                + ", cadenceWorkers=" + cadenceWorkers
                + ", kafkaBootstrapServers=" + kafkaBootstrapServers
                + ", kafkaSchemaRegistryUrl=" + kafkaSchemaRegistryUrl
                + ", kafkaAccountBalancesTopic=" + kafkaAccountBalancesTopic
                + "]";
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

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    public String getKafkaSchemaRegistryUrl() {
        return kafkaSchemaRegistryUrl;
    }

    public void setKafkaSchemaRegistryUrl(String kafkaSchemaRegistryUrl) {
        this.kafkaSchemaRegistryUrl = kafkaSchemaRegistryUrl;
    }

    public String getKafkaAccountBalancesTopic() {
        return kafkaAccountBalancesTopic;
    }

    public void setKafkaAccountBalancesTopic(String kafkaAccountBalancesTopic) {
        this.kafkaAccountBalancesTopic = kafkaAccountBalancesTopic;
    }
}
