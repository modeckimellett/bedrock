Namespace.create('CITYTECH.Utilities');

/**
 * A set of utilities for CQ.
 *
 * @class CITYTECH.Utilities
 */
CITYTECH.Utilities = {

	/**
     * Checks to see if the current wcm mode is not disabled.
     *
     * @methodOf CITYTECH.Utilities
     * @name isAuthor
     */
    isAuthor: function () {
        return Boolean(CQ.WCM);
    },

    /**
     * Checks to see if the current wcm mode is disabled.
     *
     * @methodOf CITYTECH.Utilities
     * @name isPublish
     */
    isPublish: function () {
        return !CITYTECH.Utilities.isAuthor();
    },

    /**
     * Checks to see if the current wcm mode is edit.
     *
     * @methodOf CITYTECH.Utilities
     * @name isEditMode
     */
    isEditMode: function () {
        var mode = CITYTECH.Utilities.getMode();

        return CITYTECH.Utilities.isAuthor() && (mode == 'edit' || mode == null);
    },

    /**
     * Checks to see if the current wcm mode is design.
     *
     * @methodOf CITYTECH.Utilities
     * @name isDesignMode
     */
    isDesignMode: function () {
        return CITYTECH.Utilities.isAuthor() && CITYTECH.Utilities.getMode() == 'design';
    },

    /**
     * Checks to see if the current wcm mode is preview.
     *
     * @methodOf CITYTECH.Utilities
     * @name isPreviewMode
     */
    isPreviewMode: function () {
        return CITYTECH.Utilities.isAuthor() && CITYTECH.Utilities.getMode() == 'preview';
    },

    /**
     * Returns the current mode.
     *
     * @methodOf CITYTECH.Utilities
     * @name getMode
     */
    getMode: function () {
        return $.cookie('wcmmode');
    },

    /**
     * Hide editable elements on the current page.
     *
     * @methodOf CITYTECH.Utilities
     * @name hideEditables
     * @param {Array} names The names of the editables that should be hidden
     * @param {String} comparePath The path of a page that the editables should not be hidden
     */
    hideEditables: function (names, comparePath) {
        if (CITYTECH.Utilities.isAuthor()) {
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
