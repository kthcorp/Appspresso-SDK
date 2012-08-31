/*jslint browser:true, confusion:true, debug:true, devel:true, nomen:true, plusplus:true, vars:true */
/**
 * @fileOverview This file provider W3C Widgets API
 * @version 1.0.0
 */
(function(g) {
    "use strict";
    // ====================================================

    // XXX: undocumented global variable "_APPSPRESSO_DEBUG"!
    var _DEBUG = !!g._APPSPRESSO_DEBUG;

    var ax = g.ax;// require('ax');

    var CONFIG_XML_URL = '/config.xml';

    /**
     *
     * @name widget
     * @namespace
     */
    var widget = null;

    /**
     * This class represents W3C Widgets API.
     *
     * @name Widget
     * @class
     * @constructor
     * @property {string} author
     * @property {string} description
     * @property {string} name
     * @property {string} shortName
     * @property {string} version
     * @property {string} id
     * @property {string} authorEmail
     * @property {string} authorHref
     * @property {number} width
     * @property {number} height
     * @property {*} preferences
     * @see http://www.w3.org/TR/widgets-apis/
     */
    function Widget() {
        var self = this;
        ax.util.ajax({ url:g._APPSPRESSO_CONFIG_XML_URL||CONFIG_XML_URL, method:'GET', async:false, onload:function(xhr) {
            var xmlDoc = xhr.responseXML || ax.util.parseXML(xhr.responseText);
            var rootNode = xmlDoc.documentElement;

            var id = rootNode.getAttribute('id');
            var version = rootNode.getAttribute('version');
            var width = parseInt(rootNode.getAttribute('width'), 10);
            var height = parseInt(rootNode.getAttribute('height'), 10);

            var name, shortName;
            var nameNodes = rootNode.getElementsByTagName('name');
            if(nameNodes && nameNodes.length > 0) {
                name = nameNodes[0].textContent;
                shortName = nameNodes[0].getAttribute('short');
            }

            var author, authorEmail, authorHref;
            var authorNodes = rootNode.getElementsByTagName('author');
            if(authorNodes && authorNodes.length > 0) {
                author = authorNodes[0].textContent;
                authorEmail = authorNodes[0].getAttribute('email');
                authorHref = authorNodes[0].getAttribute('href');
            }

            var description;
            var descriptionNodes = rootNode.getElementsByTagName('description');
            if(descriptionNodes && descriptionNodes.length > 0) {
                description = descriptionNodes[0].textContent;
            }

            // TODO: l10n support... author, version, shortName, name, description

            // TODO: ...

            /////////////////////////////////////////////////////////////////////////
            // preferences
            (function(widget){
                // function returnUndefined() {
                //     return undefined;
                // }
                //
                // function setPropertyMethod(prop) {
                //     ax.def(preferences).property(prop,
                //             function() {
                //         var ret = getItem.call(preferences, prop);
                //         return ret === "" || !!ret ? ret : undefined;
                //     },
                //     function(value) { setItem.call(preferenc, prop, value) });
                // }
                //
                // function removePropertyMethod(prop) {
                //     ax.def(preferences).property(prop, returnUndefined, returnUndefined);
                // }

                function length() {
                    return this.execSync('length', []);
                }
                function key(index) {
                    return this.execSync('key', [ index ]);
                }
                function getItem(key) {
                    var ret = this.execSync('getItem', [ key ]);
                    return ret;
                }
                function setItem(key, value) {
                    this.execSync('setItem', [ key, value ]);
                }
                function removeItem(key) {
                    var ret = this.execSync('removeItem', [ key ]);
                    return ret;
                }
                function clear() {
                    this.execSync('clear', []);
                }

                var preferences =
                    ax.def(ax.plugin('ax.w3.widget.preferences', {}))
                        .property('length', length)
                        .method('key', key)
                        .method('getItem', getItem)
                        .method('setItem', setItem)
                        .method('removeItem', removeItem)
                        .method('clear', clear)
                        .end();

                ax.def(widget).constant('preferences', preferences);
            })(self);

            ax.def(self)
                .constant('author', author||'')
                .constant('description', description||'')
                .constant('name', name||'')
                .constant('shortName', shortName||'')
                .constant('version', version||'')
                .constant('id', id||'')
                .constant('authorEmail', authorEmail||'')
                .constant('authorHref', authorHref||'')
                .constant('width', width)
                .constant('height', height);
        }, onerror:function(xhr, e) {
            throw ax.error(ax.UNEXPECTED_ERR, "failed to parse widget config", e);
        }});
    }

    function getWidget() {
        if(!widget) {
            widget = new Widget();
        }
        return widget;
    }

    ax.def(g).property('widget', getWidget);
    // ====================================================
}(window));
