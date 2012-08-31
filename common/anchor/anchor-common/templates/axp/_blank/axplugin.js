/*
 * JavaScript Stub Appspresso Plugin
 * 
 * id: @AXP_ID@
 * version: @AXP_VERSION@
 * feature: @AXP_FEATURE@
 */

(function(){
	function echoSync(message) {
		if(!message) {
			throw ax.error(ax.INVALID_VALUES_ERR, 'invalid argument!');
		}
		return this.execSync('echo', [ message||'' ]);
	}

	function echoAsync(callback, errback, message) {		
		if(!message) {
			throw ax.error(ax.INVALID_VALUES_ERR, 'invalid argument!');
		}
		return this.execAsync('echo', callback, errback, [ message||'' ]);
	}

	window.myplugin = ax.plugin('@AXP_ID@', {
		'echoSync': echoSync,
		'echoAsync': echoAsync
	});
})();
