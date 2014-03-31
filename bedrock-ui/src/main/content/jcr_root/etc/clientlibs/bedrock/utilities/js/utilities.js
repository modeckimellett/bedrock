/**
 * @namespace
 */
var Bedrock = Bedrock || {};

/**
 * A set of utilities for CQ.
 *
 * @namespace
 */
Bedrock.Utilities = {

	/**
     * Checks to see if the current wcm mode is not disabled.
     */
    isAuthor: function () {
        return Boolean(CQ.WCM);
    },

    /**
     * Checks to see if the current wcm mode is disabled.
     */
    isPublish: function () {
        return !Bedrock.Utilities.isAuthor();
    },

    /**
     * Checks to see if the current wcm mode is edit.
     */
    isEditMode: function () {
        var mode = Bedrock.Utilities.getMode();

        return Bedrock.Utilities.isAuthor() && (mode == 'edit' || mode == null);
    },

    /**
     * Checks to see if the current wcm mode is design.
     */
    isDesignMode: function () {
        return Bedrock.Utilities.isAuthor() && Bedrock.Utilities.getMode() == 'design';
    },

    /**
     * Checks to see if the current wcm mode is preview.
     */
    isPreviewMode: function () {
        return Bedrock.Utilities.isAuthor() && Bedrock.Utilities.getMode() == 'preview';
    },

    /**
     * Returns the current mode.
     */
    getMode: function () {
        return $.cookie('wcmmode');
    },

    /**
     * Hide editable elements on the current page.
     *
     * @param {Array} names The names of the editables that should be hidden
     * @param {String} comparePath The path of a page that the editables should not be hidden
     */
    hideEditables: function (names, comparePath) {
        if (Bedrock.Utilities.isAuthor()) {
            var pagePath = CQ.WCM.getPagePath();
            var noCompare = typeof comparePath === 'undefined';

            $.each(names, function (i, name) {
                var path = pagePath + '/jcr:content/' + name;

                CQ.WCM.onEditableReady(path, function (dialog) {
                    if (noCompare) {
                        dialog.hide();
                    } else {
                        if (pagePath != comparePath) {
                            dialog.hide();
                        }
                    }
                });
            });
        }
    }
};
