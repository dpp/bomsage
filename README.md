# BOM Sage

The Open, Federated Source of Software Truth

Knowing what makes up the software that controls your business, your house, your
entertainment, your car, etc. is critical to understanding the security posture
and risks associated with the systems that control our lives.

In May, 2021, the United States issued an [Executive Order](https://www.whitehouse.gov/briefing-room/presidential-actions/2021/05/12/executive-order-on-improving-the-nations-cybersecurity/)
requiring Software Bill of Materials (SBOMs). The justification (Sec. 10(j)):

> the term “Software Bill of Materials” or “SBOM” means a formal record containing the details and supply chain 
> relationships of various components used in building software.  Software developers and vendors often create 
> products by assembling existing open source and commercial software components.  The SBOM enumerates these 
> components in a product.  It is analogous to a list of ingredients on food packaging.  An SBOM is useful 
> to those who develop or manufacture software, those who select or purchase software, and those who operate software.
> Developers often use available open source and third-party software components to create a product; an SBOM 
> allows the builder to make sure those components are up to date and to respond quickly to new vulnerabilities.  
> Buyers can use an SBOM to perform vulnerability or license analysis, both of which can be used to evaluate risk 
> in a product.  Those who operate software can use SBOMs to quickly and easily determine whether they are at 
> potential risk of a newly discovered vulnerability.   A widely used, machine-readable SBOM format 
> allows for greater benefits through automation and tool integration.  The SBOMs gain greater value when 
> collectively stored in a repository that can be easily queried by other applications and systems.  
> Understanding the supply chain of software, obtaining an SBOM, and using it to analyze known 
> vulnerabilities are crucial in managing risk.

There exist a number of standards and proposed standards for SBOMs including [GitBOM](https://gitbom.dev/),
[SPDX](https://spdx.dev/), [CycloneDX](https://cyclonedx.org/), etc.

SPDX and CycloneDX are semi-competing while GitBOM augments both with a cryptographic (Merkle) directed acyclic
graph (DAG) of the binaries and files that compose a system.

## Aggregating the SBOMs

The executive order only requires that SBOMs be "provid[ed to] a purchaser a Software Bill of Materials (SBOM) for each product directly or 
by publishing it on a public website[.]"

However, to be able to track threats (e.g. [Log4j](https://www.cisa.gov/uscert/apache-log4j-vulnerability-guidance))
across systems, there must be an aggregation of SBOMs such that threat information can be derived and understood
across the whole of software systems.

Such a global repository much be open and federated, but also cryptographically trusted and human governable.

Thus BOM Sage.

For more, please read the [vision](info/vision.md)

## Pariticipating

This is an open source project and participation is encouraged and governed by the (Code of Conduct)[code_of_conduct.md].

## License

The contents of this repository is licened under an Apache 2.0 license.
