package fr.abes.derives.cli;


public class WorkerException extends Exception {

	private Exception exception = null;
	private String absolutePath = null;

	public WorkerException(Exception exception, String absolutePath) {
		super();
		this.exception = exception;
		this.absolutePath = absolutePath;
	}

	public Exception getException() {
		return exception;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	@Override
	public String getMessage() {
		return this.exception.getMessage()+"("+this.absolutePath+")";
	}


}
