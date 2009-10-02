<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_chess_state">
			<xsl:with-param name="Width">3</xsl:with-param>
			<xsl:with-param name="Height">30</xsl:with-param>
			<xsl:with-param name="CellWidth">46</xsl:with-param>
			<xsl:with-param name="CellHeight">12</xsl:with-param>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
			<xsl:with-param name="checkered">invisible</xsl:with-param>
			<xsl:with-param name="CellFluentName">BLOCK</xsl:with-param>
			<xsl:with-param name="BorderWidth">1</xsl:with-param>
			<xsl:with-param name="BorderStyle">solid #000000</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="xArg"/>
		<xsl:param name="yArg"/>

		<div>
			<xsl:attribute name="style">
 				background-color: rgb(<xsl:value-of select="(30-$yArg)*8+15"/>, 0, 0);
 				width: 44px;
 				height: 10px;
 			</xsl:attribute>
		</div>
	</xsl:template>
	
</xsl:stylesheet>