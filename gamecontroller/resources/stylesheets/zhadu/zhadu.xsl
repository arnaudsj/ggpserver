<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	<xsl:import href="../generic/sitespecific.xsl"/>

	<xsl:template name="print_state">
		<style type="text/css" media="all">
			div.zhadu_board {
				position:relative;
				width: 301px;
				height: 551px;
				padding: 0px;
			}
			div.zhadu_piece {
				position: absolute;
				padding:  0px;
			}
		</style>
		<!-- Draw Board -->
		<div class="zhadu_board">
			<img>
				<xsl:attribute name="src"><xsl:value-of select="concat($stylesheetURL,'/zhadu/zhadu_board.png')"/></xsl:attribute>
			</img>
			<xsl:for-each select="fact[prop-f='PIECE']">
				<xsl:variable name="pos" select="arg[3]"/>
				<xsl:variable name="owner" select="arg[1]"/>
				<xsl:variable name="value" select="./arg[2]"/>
				<xsl:variable name="alt">(PIECE<xsl:text> </xsl:text><xsl:value-of select="$owner"/><xsl:text> </xsl:text><xsl:value-of select="$value"/><xsl:text> </xsl:text><xsl:value-of select="$pos"/>)</xsl:variable>
				<xsl:variable name="triangle" select="translate($pos, '123', '')"/>
				<xsl:variable name="position_in_triangle" select="translate($pos, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', '')"/>
				
				<xsl:variable name="offset_piece">
					<xsl:choose>
						<xsl:when test="$value='1'">-14</xsl:when>
						<xsl:when test="$value='2'">-17</xsl:when>
						<xsl:when test="$value='3' or $value='123'">-20</xsl:when>
						<xsl:when test="$value='4'">-23</xsl:when>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="posx_triangle">
					<xsl:choose>
						<xsl:when test="$triangle='A' or $triangle='C' or $triangle='F' or $triangle='H'">150</xsl:when>
						<xsl:when test="$triangle='B' or $triangle='E'">75</xsl:when>
						<xsl:when test="$triangle='D' or $triangle='G'">227</xsl:when>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="posx_offset">
					<xsl:choose>
						<xsl:when test="$position_in_triangle='1' or $position_in_triangle=''">0</xsl:when>
						<xsl:when test="($position_in_triangle='2' and ($triangle='A' or $triangle='B' or $triangle='D' or $triangle='F')) or
										($position_in_triangle='3' and ($triangle='C' or $triangle='E' or $triangle='G' or $triangle='H'))">-37</xsl:when>
						<xsl:otherwise>37</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="posx" select="number($posx_triangle)+number($posx_offset)+number($offset_piece)"/>
				<xsl:variable name="posy_triangle">
					<xsl:choose>
						<xsl:when test="$triangle='A'">96</xsl:when>
						<xsl:when test="$triangle='B' or $triangle='D'">229</xsl:when>
						<xsl:when test="$triangle='C'">183</xsl:when>
						<xsl:when test="$triangle='F'">363</xsl:when>
						<xsl:when test="$triangle='E' or $triangle='G'">318</xsl:when>
						<xsl:when test="$triangle='H'">452</xsl:when>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="posy_offset">
					<xsl:choose>
						<xsl:when test="$position_in_triangle=''">0</xsl:when>
						<xsl:when test="$position_in_triangle='1' and ($triangle='A' or $triangle='B' or $triangle='D' or $triangle='F')">-44</xsl:when>
						<xsl:when test="$position_in_triangle='1' and ($triangle='C' or $triangle='E' or $triangle='G' or $triangle='H')">44</xsl:when>
						<xsl:when test="$triangle='A' or $triangle='B' or $triangle='D' or $triangle='F'">21</xsl:when>
						<xsl:when test="$triangle='C' or $triangle='E' or $triangle='G' or $triangle='H'">-21</xsl:when>
						<xsl:otherwise>35</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:variable name="posy" select="number($posy_triangle)+number($posy_offset)+number($offset_piece)"/>
		    	
		    	<div class="zhadu_piece">
					<xsl:attribute name="style">
						left: <xsl:value-of select="$posx"/>px;
						top: <xsl:value-of select="$posy"/>px;
					</xsl:attribute>
					<img>
						<xsl:attribute name="src"><xsl:value-of select="concat($stylesheetURL, '/zhadu/zhadu_stone_', translate($owner, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '_', $value, '.png')"/></xsl:attribute>
						<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
						<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
					</img>
				</div>
			</xsl:for-each>
		</div>
		
		<!-- show remaining fluents -->
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'PIECE'"/>
			<xsl:with-param name="excludeFluent2" select="'CONTROL'"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
