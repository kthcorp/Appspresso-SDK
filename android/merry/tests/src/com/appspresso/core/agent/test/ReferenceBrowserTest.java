package com.appspresso.core.agent.test;

import android.test.ActivityInstrumentationTestCase2;

import com.appspresso.core.agent.ReferenceBrowser;
import com.appspresso.core.runtime.widget.WidgetAgent;

public class ReferenceBrowserTest extends
		ActivityInstrumentationTestCase2<ReferenceBrowser> {

	private ReferenceBrowser activity;

	public ReferenceBrowserTest() {
		super("com.appspresso.core.agent", ReferenceBrowser.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
	}

	public void testResources() {
		assertEquals("Ax-Going-Merry",
				activity.getString(com.appspresso.core.agent.R.string.ax_name));
	}

	public void testWidgetAgent() {
		WidgetAgent agent = activity.getWidgetAgent();
		assertNotNull("activity should have an agent", agent);
		assertNotNull("agent should have a widget view", agent.getWidgetView());
		assertNotNull("agent should have a widget", agent.getWidget());
		assertNotNull("agent should have a plugin manager",
				agent.getPluginManager());
		assertNotNull("agent should have a runtime context",
				agent.getAxRuntimeContext());
		assertNotNull("agent should have a plugin manager",
				agent.getPluginManager());
		assertNotNull("agent should have a base dir", agent.getBaseDir());
	}

}
