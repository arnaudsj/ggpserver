<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	Knightstour
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	<xsl:import href="../generic/state.xsl"/>

	<xsl:template name="print_state">
		
		<!-- paint board -->
		<xsl:call-template name="chess_board">
			<xsl:with-param name="Width">6</xsl:with-param>
			<xsl:with-param name="Height">5</xsl:with-param>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
			<xsl:with-param name="CellFluentName" select="'CELL'"/>
		</xsl:call-template>

		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'CELL'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="make_cell_content">
		<xsl:param name="content"/>
		<xsl:param name="background"/>

		<xsl:variable name="piece">
			<xsl:choose>
				<xsl:when test="$content='KNIGHT'">al</xsl:when>
				<xsl:when test="$content='HOLE'">ad</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:call-template name="make_chess_img">
			<xsl:with-param name="piece" select="$piece"/>
			<xsl:with-param name="background" select="$background"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>