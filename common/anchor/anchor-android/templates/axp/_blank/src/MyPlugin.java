package @PACKAGE_NAME@;

import com.appspresso.api.*;

/**
 * TODO: change package and class name.
 *
 * Appspresso Plugin Android Module
 *
 *
 */
public class @JAVA_NAME@ implements AxPlugin {

	private AxRuntimeContext runtimeContext;

	@Override
	public void activate(AxRuntimeContext runtimeContext) {
		this.runtimeContext = runtimeContext;

		// TODO: addActivityListener
		// TODO: addWebViewListener
	}

	@Override
	public void deactivate(AxRuntimeContext runtimeContext) {
		// TODO: removeActivityListener
		// TODO: removeWebViewListener
		
		this.runtimeContext = null;
	}

	@Override
	public void execute(AxPluginContext context) {
		String method = context.getMethod();

		if ("echo".equals(method)) {
			String message = context.getParamAsString(0, null);
			context.sendResult(message);
		}
		else {
			context.sendError(AxError.NOT_AVAILABLE_ERR);
		}
	}

}