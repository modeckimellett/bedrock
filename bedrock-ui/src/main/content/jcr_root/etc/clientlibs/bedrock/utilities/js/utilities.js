Namespace.create('Bedrock.Utilities');

/**
 * A set of utilities for CQ.
 *
 * @class Bedrock.Utilities
 */
Bedrock.Utilities = {

	/**
     * Checks to see if the current wcm mode is not disabled.
     *
     * @methodOf Bedrock.Utilities
     * @name isAuthor
     */
    isAuthor: function () {
        return Boolean(CQ.WCM);
    },

    /**
     * Checks to see if the current wcm mode is disabled.
     *
     * @methodOf Bedrock.Utilities
     * @name isPublish
     */
    isPublish: function () {
        return !Bedrock.Utilities.isAuthor();
    },

    /**
     * Checks to see if the current wcm mode is edit.
     *
     * @methodOf Bedrock.Utilities
     * @name isEditMode
     */
    isEditMode: function () {
        var mode = Bedrock.Utilities.getMode();

        return Bedrock.Utilities.isAuthor() && (mode == 'edit' || mode == null);
    },

    /**
     * Checks to see if the current wcm mode is design.
     *
     * @methodOf Bedrock.Utilities
     * @name isDesignMode
     */
    isDesignMode: function () {
        return Bedrock.Utilities.isAuthor() && Bedrock.Utilities.getMode() == 'design';
    },

    /**
     * Checks to see if the current wcm mode is preview.
     *
     * @methodOf Bedrock.Utilities
     * @name isPreviewMode
     */
    isPreviewMode: function () {
        return Bedrock.Utilities.isAuthor() && Bedrock.Utilities.getMode() == 'preview';
    },

    /**
     * Returns the current mode.
     *
     * @methodOf Bedrock.Utilities
     * @name getMode
     */
    getMode: function () {
        return $.cookie('wcmmode');
    },

    /**
     * Hide editable elements on the current page.
     *
     * @methodOf Bedrock.Utilities
     * @name hideEditables
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
