Ringo - the PEPPOL Access Point back end
========================================


[TOC levels=2-5]: # "### Table of contents"
### Table of contents
- [Installation](#installation)
- [Possible problems and how to solve them](#possible-problems-and-how-to-solve-them)

This repository contains, "Ringo", a REST-based system for managing the "back-end" for a PEPPOL Access Point running
[Oxalis](https://github.com/difi/oxalis).

Ringo was originally developed by Steinar Overbeck Cook, [SendRegning](http://www.sendregning.no/), which was
taken over by Unit4 in 2012. Unit4 has kindly donated the software to
the Norwegian agency for Public Management and eGovernment (Difi), which now looks after the software.

_Ringo_ was developed to be used with the MySQL database. It has been confirmed to work with H2
and Microsoft SQL Server as well. If you would like to use a different database, please
contribute and send us a pull request.

## Installation

Please refer to the [Installation guide](/INSTALL.md)

## Troubleshooting

* Receivers PEPPOL certificate expired
* Invalid SSL certificate
* Access Point not available
* Non-existent receiver in SMP
