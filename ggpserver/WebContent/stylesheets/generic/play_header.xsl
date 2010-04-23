<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for drawing the generic game-master header.
	- For use within <body>.
	- needs css/main.css and sitespecific.xsl
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="header.xsl"/>

	<xsl:template name="play_header">
		<div class="header" id="header">
			<xsl:call-template name="print_match_info"/>
			<div class="underline" style="clear:left;"/>
		</div>
	</xsl:template>

</xsl:stylesheet>