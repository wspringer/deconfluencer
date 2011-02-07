========
 README
========


What is it?
===========

Deconfluencer is a tool for rewriting Confluence pages: it basically
allows you to strip off all window decorations, take out links,
rewrite links, you name it.


Limitations
===========

The current version is able to serve data from a local directory, or
from Confluence. However, at this stage, it assumes all data to be
HTML. That is, it doesn't have a way of proxying non-HTML based data
yet.

How to build
============

You build Deconfluencer exactly the way you would expect to build a
Maven project::

  mvn install

The target directory will contain a tar.gz file containing everything
required. Just unzip that file in a directory of your preference, and
you're in business.

How to user it
==============

That's simple. Simply type::

  deconfluencer

It has couple of required options. First of all, it wants you to
explain how to map incoming URLs to Confluence URLs, by passing it the
base URL of the Confluence site you are proxying. For every incoming
request, the path will be stripped of and combined with this base URL.

However, normally Confluence will not allow for anonymous access. That
means you will have to pass a username and password as well.

The 'rewriting' bit is actually done by an XSLT stylesheet. By
default, it will pick filter.xsl from the conf directory. However, you
can point the deconfluencer to any XSL you like. 

In many cases, your new design will require some static resources. In
order to make those available, you would normally have to put them on
another web server. However, with the deconfluencer, that's not
required. You can just pass in the directory containing your static
resources. These resources can then be addressed by putting in
'/resources' in front of their names.

