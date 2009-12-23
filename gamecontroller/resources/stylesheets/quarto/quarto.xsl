<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/chess_board.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="chess_board">
			<xsl:with-param name="DefaultCellContent">no</xsl:with-param>
			<xsl:with-param name="checkered">no</xsl:with-param>
		</xsl:call-template>
		
		<span class="heading">Selected:</span><br/>
		<div style="height:44px;">
			<xsl:for-each select="fact[prop-f='SELECTED']">
				<xsl:variable name="alt"><xsl:call-template name="fluent2text"/></xsl:variable>
				<xsl:call-template name="make_cell_content">
					<xsl:with-param name="content" select="arg[1]"/>
					<xsl:with-param name="alt" select="$alt"/>
				</xsl:call-template>
			</xsl:for-each>
		</div>
		
		<span class="heading">Pool:</span><br/>
		<div style="width: 176px; height: 200px;">
			<xsl:for-each select="fact[prop-f='POOL']">
				<xsl:variable name="alt"><xsl:call-template name="fluent2text"/></xsl:variable>
				<xsl:call-template name="make_cell_content">
					<xsl:with-param name="content" select="arg[1]"/>
					<xsl:with-param name="alt" select="$alt"/>
				</xsl:call-template>
			</xsl:for-each>
		</div>
		
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent">CELL</xsl:with-param>
			<xsl:with-param name="excludeFluent2">SELECTED</xsl:with-param>
			<xsl:with-param name="excludeFluent3">POOL</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="make_cell_content">
		<xsl:param name="content"/>
		<xsl:param name="alt"/>

		<xsl:if test="$content!='EMPTY'">
			<xsl:variable name="bit3" select="substring($content,2,1)"/>
			<xsl:variable name="bit2" select="substring($content,3,1)"/>
			<xsl:variable name="bit1" select="substring($content,4,1)"/>
			<xsl:variable name="bit0" select="substring($content,5,1)"/>
			
			<xsl:variable name="piecename">
				<xsl:choose>
					<xsl:when test="$bit3='1' and $bit2='1'">n</xsl:when>
					<xsl:when test="$bit3='1' and $bit2='0'">s</xsl:when>
					<xsl:when test="$bit3='0' and $bit2='1'">p</xsl:when>
					<xsl:when test="$bit3='0' and $bit2='0'">h</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="piececolor">
				<xsl:choose>
					<xsl:when test="$bit1='1'">l</xsl:when>
					<xsl:when test="$bit1='0'">d</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="background">
				<xsl:choose>
					<xsl:when test="$bit0='1'">light</xsl:when>
					<xsl:when test="$bit0='0'">dark</xsl:when>
				</xsl:choose>
			</xsl:variable>
	
			<xsl:call-template name="make_chess_img">
				<xsl:with-param name="piece" select="concat($piecename, $piececolor)"/>
				<xsl:with-param name="background" select="$background"/>
				<xsl:with-param name="alt" select="$alt"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>