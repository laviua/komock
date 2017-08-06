package ua.com.lavi.komock.model.odm;

/**
 * Created by Oleksandr Loushkin on 06.08.17.
 */
@SuppressWarnings("PMD")
public class OdmRemoteApiResponse {

    private String uuid;
    private RiskCheckStatus status;
    private String failedReason;

    public OdmRemoteApiResponse(String uuid, RiskCheckStatus status) {
        this.uuid = uuid;
        this.status = status;
    }

    public OdmRemoteApiResponse(String uuid, RiskCheckStatus status, String failedReason) {
        this.uuid = uuid;
        this.status = status;
        this.failedReason = failedReason;
    }

    public String getUuid() {
        return uuid;
    }

    public RiskCheckStatus getStatus() {
        return status;
    }

    public String getFailedReason() {
        return failedReason;
    }

    @Override
    public String toString() {
        return "{" +
                "uuid='" + uuid + '\'' +
                ", status=" + status +
                ", failedReason='" + failedReason + '\'' +
                '}';
    }
}