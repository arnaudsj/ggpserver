<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:variable name="playing">
		<xsl:choose>
			<xsl:when test="count(/match/legalmoves) = 1">1</xsl:when>
			<xsl:otherwise>0</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:variable name="currentStep" select="count(/match/history/step)+1"/>

	<xsl:variable name="role" select="/match/sight-of"/>

</xsl:stylesheet>
