<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	works for chinesecheckers[1-6], ...
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_chinesecheckers_board"/>
		
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'CELL'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="print_chinesecheckers_board">
		<style type="text/css" media="all">
			div.chinesecheckersboard
			{
				position:   relative;
				width:      350px;
				height:     350px;
				padding:    10px;
				border:     2px solid #b17735;
				background: transparent url(
				<xsl:value-of select="$stylesheetURL"/>/chinesecheckers/ccboard.gif
				) repeat top left;
			}
			div.chinesecheckersboard div.teal
			{
				width: 50px;
				height: 40px;
				background: #00ffff;
			}
			div.chinesecheckersboard div.red
			{
				width: 50px;
				height: 40px;
				background: #ff0000;
			}
			div.chinesecheckersboard div.blue
			{
				width: 50px;
				height: 40px;
				background: #0000ff;
			}
			div.chinesecheckersboard div.green
			{
				width: 50px;
				height: 40px;
				background: #00ff00;
			}
			div.chinesecheckersboard div.yellow
			{
				width: 50px;
				height: 40px;
				background: #ffff00;
			}
			div.chinesecheckersboard div.magenta
			{
				width: 50px;
				height: 40px;
				background: #ff00ff;
			}
			div.chinesecheckersboard div.blank
			{
				width: 50px;
				height: 40px;
				background: #cca083;
			}
			div.chinesecheckersboard #B2,#C2,#C3,#C4,#C5,#C6,#C7,#D2,#D3,#D4,#D5,#D6,#E2,#E3,#E4,#E5,#F2,#F3,#F4,#F5,#F6,#G2,#G3,#G4,#G5,#G6,#G7,#H2
			{
				float: left;
			}
			div.chinesecheckersboard #A1,#I1
			{
				margin-left: 150px;
				clear: left;
			}
			div.chinesecheckersboard #B1,#H1
			{
				margin-left: 125px;
				float: left;
				clear: left;
			}
			div.chinesecheckersboard #C1,#G1
			{
				margin-left: 0px;
				clear: left;
				float: left;
			}
			div.chinesecheckersboard #D1,#F1
			{
				margin-left: 25px;
				clear: left;
				float: left;
			}
			div.chinesecheckersboard #E1
			{
				margin-left: 50px;
				clear: left;
				float: left;
			}
		</style>

		<div class="chinesecheckersboard">
			<xsl:for-each select="fact[prop-f='CELL']">
				<xsl:sort select="arg[1]"/>
				<xsl:variable name="alt"><xsl:call-template name="fluent2text"/></xsl:variable>
				<div>
					<xsl:attribute name="id"><xsl:value-of select="arg[1]"/></xsl:attribute>
					<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
					<xsl:variable name="up" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
					<xsl:variable name="lo" select="'abcdefghijklmnopqrstuvwxyz'"/>
					<xsl:attribute name="class"><xsl:value-of select="translate(arg[2],$up,$lo)"/></xsl:attribute>
					<xsl:choose>
						<xsl:when test="arg[2]='BLANK'">
							<img>
								<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>chinesecheckers/boardpitb.gif</xsl:attribute>
								<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
								<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
							</img>
						</xsl:when>
						<xsl:otherwise> <!-- arg[2] is a color -->
							<img>
								<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>chinesecheckers/boardpit.gif</xsl:attribute>
								<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
								<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
							</img>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</xsl:for-each>
		</div>
	
	</xsl:template>

</xsl:stylesheet>
