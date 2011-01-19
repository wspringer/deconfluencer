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

The current version is configured with a default stylesheet defining
the page rewriting tools and a default location to forward too. That's
something that will probably change at some point in time. Other than
that, it's not tested too much, and logging sucks too. But it works.

How to build
============

You build Deconfluencer exactly the way you would expect to build a
Maven project::

  mvn install

Currently, it's not generating a tar.gz file or anything yet, so you
will actually have to go into the target directoy, and the
appassembler directory to start it.

How to user it
==============

That's simple. Simply type::

  deconfluencer

That will give you a list of options. There are two options required:
the username, and a password. That's it. It will start the
deconfluencer as a reverse proxy on a certain port and forward all
incoming calls to confluence, hopefully translating the results into
something that makes sense. (So you need to point your browser to a
URL that includes the portnumber on which you have the deconfluencer
running.)



