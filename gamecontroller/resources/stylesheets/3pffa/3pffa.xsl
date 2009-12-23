<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	3pffa
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="Width">7</xsl:with-param>
			<xsl:with-param name="Height">7</xsl:with-param>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
			<xsl:with-param name="CellFluentName">CELL</xsl:with-param>
			<xsl:with-param name="checkered">alldark</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="xArg"/>
		<xsl:param name="yArg"/>
		<xsl:param name="piece"/>
		<xsl:param name="background"/>
		<xsl:param name="alt"/>

		<xsl:variable name="background2">
			<xsl:choose>
				<xsl:when test="($xArg='1' and $yArg='4') or ($xArg='4' and $yArg='1') or ($xArg='7' and $yArg='4')">light</xsl:when>
				<xsl:otherwise><xsl:value-of select="$background"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="make_chess_img">
			<xsl:with-param name="piece" select="$piece"/>
			<xsl:with-param name="background" select="$background2"/>
			<xsl:with-param name="alt" select="$alt"/>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>