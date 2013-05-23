# CITYTECH, Inc. CQ5 Library

[CITYTECH, Inc.](http://www.citytechinc.com)

## Overview

The CITYTECH CQ5 Library contains common utilities, decorators, abstract classes, tag libraries, and JavaScript modules for bootstrapping and simplifying Adobe CQ5 implementations.

## `Optional` Usage

Many of the component and page decorator methods now return an `Optional` wrapper for nullable values.  See the [Google Guava user guide](https://code.google.com/p/guava-libraries/wiki/UsingAndAvoidingNullExplained#Optional) for a more thorough explanation, but here are a few examples of `Optional` usage in a CQ context:

    String href = getAsHref("path").or("#");
    String redirectHref = getAsHref("redirectPath").or(currentPage.getHref());

    Optional<PageDecorator> pageOptional = getAsPage("pagePath");

    if (pageOptional.isPresent()) {
        PageDecorator page = pageOptional.get();
        String title = page.getNavigationTitleOptional().or(page.getTitle());

        ...
    }

The [Javadoc](http://docs.guava-libraries.googlecode.com/git-history/release/javadoc/com/google/common/base/Optional.html) has the full list of available methods.

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.

Previous releases had the CQ version number appended to the library version, e.g. "2.0.0-cq-5.4.0".  Going forward, the CQ version number will be omitted in favor of a simplified scheme using the above guidelines.  A new version of CQ would introduce dependency version changes and possibly backwards incompatibility, which would correspond to a major version increment.  Absent other significant library API changes, only minor and patch version changes would apply for a single version of CQ.

## License

Copyright 2013, CITYTECH, Inc.