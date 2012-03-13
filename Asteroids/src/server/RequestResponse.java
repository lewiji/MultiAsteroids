package server;

public abstract class RequestResponse {
	private int entityId;

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}
}
