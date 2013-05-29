Namespace.create('CITYTECH.Utilities');

CITYTECH.Utilities = function () {
    return {
        isAuthor: function () {
            return Boolean(CQ.WCM);
        },

        isPublish: function () {
            return !CITYTECH.Utilities.isAuthor();
        },

        isEditMode: function () {
            var mode = CITYTECH.Utilities.getMode();

            return CITYTECH.Utilities.isAuthor() && (mode == 'edit' || mode == null);
        },

        isDesignMode: function () {
            return CITYTECH.Utilities.isAuthor() && CITYTECH.Utilities.getMode() == 'design';
        },

        isPreviewMode: function () {
            return CITYTECH.Utilities.isAuthor() && CITYTECH.Utilities.getMode() == 'preview';
        },

        getMode: function () {
            return $.cookie('wcmmode');
        },

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
}();