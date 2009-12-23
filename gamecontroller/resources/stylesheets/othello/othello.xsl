<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	similar to chess_like but doesn't print green cells
	works for *othello*, ...
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="chess_board">
			<xsl:with-param name="Width" select="'8'"/>
			<xsl:with-param name="Height" select="'8'"/>
			<xsl:with-param name="checkered">alldark</xsl:with-param>
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
			<xsl:with-param name="DefaultCell">no</xsl:with-param>
			<xsl:with-param name="CellFluentName">CELL</xsl:with-param>
			<xsl:with-param name="xArgIdx">2</xsl:with-param>
			<xsl:with-param name="yArgIdx">1</xsl:with-param>
		</xsl:call-template>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'CELL'"/>
			<xsl:with-param name="excludeFluent2" select="'FRINGE'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell">
		<xsl:param name="col"/>
		<xsl:param name="row"/>
		<xsl:param name="defaultClass"/>
		<div>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="fact[prop-f='CELL' and arg[3]='GREEN'] and not(fact[prop-f='CELL' and contains(arg[1],$row) and contains(arg[2],$col)])">cellInvisible</xsl:when>
					<xsl:when test="fact[prop-f='FRINGE' and substring-after(arg[1],'C')=$row and substring-after(arg[2],'C')=$col] and 
									(fact[prop-f='ROWSELECTED' and substring-after(arg[1],'C')=$row] or not(fact[prop-f='ROWSELECTED']))">cellLight</xsl:when>
					<xsl:otherwise><xsl:value-of select="$defaultClass"/></xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
		</div>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="content"/>
		<xsl:param name="piece"/>
		<xsl:param name="background"/>
		<xsl:param name="alt"/>

		<xsl:choose>
			<xsl:when test="$content='GREEN'"/>
			<xsl:otherwise>
				<xsl:call-template name="make_chess_img">
					<xsl:with-param name="piece" select="$piece"/>
					<xsl:with-param name="background" select="$background"/>
					<xsl:with-param name="alt" select="$alt"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>