<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	site specific definitions (location of the stylesheets and URL of links to xml of other steps)
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:variable name="stylesheetURL">../../stylesheets/</xsl:variable> <!-- absolute url or relative to xml of matches -->

	<xsl:template name="makeStepLinkURL">
		<xsl:param name="step"/> <!-- an integer number >=1 or 'final' -->
		<xsl:param name="role"/> <!-- the player who sees the game field -->
		<xsl:param name="seconds"/> <!-- for the play mode, adds how many seconds we will wait to show next step -->
		<xsl:choose>
			<xsl:when test="$step='final'">finalstate.xml</xsl:when>
			<xsl:otherwise>step_<xsl:value-of select="$step"/>.xml</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="$seconds != ''">
			<xsl:text disable-output-escaping="yes">&#63;seconds=</xsl:text>
			<xsl:value-of select="$seconds"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="webmaster">
		???
	</xsl:template>
</xsl:stylesheet>
