<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	generic style sheet (just prints a list of fluents)

	To make your own stylesheet change the print_state template to output the state the given position and (if neccessary) change the stateWidth parameter in the main template
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="template.xsl"/>
	<xsl:import href="state.xsl"/>

	<xsl:template name="print_state">
		<xsl:call-template name="state"/>
	</xsl:template>

</xsl:stylesheet>
