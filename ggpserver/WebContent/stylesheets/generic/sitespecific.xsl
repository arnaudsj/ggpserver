<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	site specific definitions (location of the stylesheets and URL of links to xml of other steps)
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:variable name="stylesheetURL">../stylesheets/</xsl:variable> <!-- absolute url or relative to xml of matches -->

	<xsl:template name="makeStepLinkURL">
		<xsl:param name="step"/> <!-- an integer number >=1 or 'final' -->
		<xsl:param name="role"/> <!-- the player who sees the game field -->
		<xsl:param name="pathPrefix"/> <!-- because working with relative URLs, this can be useful (e.g. for call from history, while playing) -->
		<xsl:value-of select="$pathPrefix"/>
		<xsl:text disable-output-escaping="yes">view_state.jsp?matchID=</xsl:text>
		<xsl:value-of select="/match/match-id"/>
		<xsl:text disable-output-escaping="yes">&amp;stepNumber=</xsl:text>
		<xsl:value-of select="$step"/>
		<xsl:text disable-output-escaping="yes">&amp;role=</xsl:text>
		<xsl:value-of select="$role"/>
	</xsl:template>
	
	<xsl:template name="makePlayLinkURL">
		<xsl:param name="forStepNumber" /> <!-- the step for which the following action is meant -->
		<xsl:param name="chosenMove" /> <!-- the chosen action (chosen move number or -1 to confirm) -->
		<xsl:text disable-output-escaping="yes">play.jsp?matchID=</xsl:text>
		<xsl:value-of select="/match/match-id" />
		<xsl:text disable-output-escaping="yes">&amp;forStepNumber=</xsl:text>
		<xsl:value-of select="$forStepNumber" />
		<xsl:text disable-output-escaping="yes">&amp;chosenMove=</xsl:text>
		<xsl:value-of select="$chosenMove" />
	</xsl:template>

	<xsl:template name="webmaster">
		<a href="http://www.inf.tu-dresden.de/index.php?node_id=1373&amp;ln=en">Stephan Schiffel</a>
	</xsl:template>
</xsl:stylesheet>
