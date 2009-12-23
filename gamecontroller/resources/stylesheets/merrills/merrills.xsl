<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>
	<xsl:import href="../generic/state.xsl"/>
	
	<xsl:template name="print_state">
		<xsl:call-template name="print_merrills_board"/>
		
		<xsl:call-template name="state">
			<xsl:with-param name="excludeFluent" select="'PIT'"/>
			<xsl:with-param name="excludeFluent2" select="'HEAP'"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="print_merrills_board">
		<style type="text/css" media="all">
		
			div.merrillsboard div.OUTER_NW { position: absolute; top:  -3px; left:  -3px;}
			div.merrillsboard div.OUTER_N  { position: absolute; top:  -3px; left: 117px;}
			div.merrillsboard div.OUTER_NE { position: absolute; top:  -3px; left: 237px;}
			div.merrillsboard div.OUTER_W  { position: absolute; top: 117px; left:  -3px;}
			div.merrillsboard div.OUTER_E  { position: absolute; top: 117px; left: 237px;}
			div.merrillsboard div.OUTER_SW { position: absolute; top: 237px; left:  -3px;}
			div.merrillsboard div.OUTER_S  { position: absolute; top: 237px; left: 117px;}
			div.merrillsboard div.OUTER_SE { position: absolute; top: 237px; left: 237px;}
			div.merrillsboard div.MIDDLE_NW{ position: absolute; top:  37px; left:  37px;}
			div.merrillsboard div.MIDDLE_N { position: absolute; top:  37px; left: 117px;}
			div.merrillsboard div.MIDDLE_NE{ position: absolute; top:  37px; left: 197px;}
			div.merrillsboard div.MIDDLE_W { position: absolute; top: 117px; left:  37px;}
			div.merrillsboard div.MIDDLE_E { position: absolute; top: 117px; left: 197px;}
			div.merrillsboard div.MIDDLE_SW{ position: absolute; top: 197px; left:  37px;}
			div.merrillsboard div.MIDDLE_S { position: absolute; top: 197px; left: 117px;}
			div.merrillsboard div.MIDDLE_SE{ position: absolute; top: 197px; left: 197px;}
			div.merrillsboard div.INNER_NW { position: absolute; top:  77px; left:  77px;}
			div.merrillsboard div.INNER_N  { position: absolute; top:  77px; left: 117px;}
			div.merrillsboard div.INNER_NE { position: absolute; top:  77px; left: 157px;}
			div.merrillsboard div.INNER_W  { position: absolute; top: 117px; left:  77px;}
			div.merrillsboard div.INNER_E  { position: absolute; top: 117px; left: 157px;}
			div.merrillsboard div.INNER_SW { position: absolute; top: 157px; left:  77px;}
			div.merrillsboard div.INNER_S  { position: absolute; top: 157px; left: 117px;}
			div.merrillsboard div.INNER_SE { position: absolute; top: 157px; left: 157px;}
			
			div.merrillsboard{
				position: relative;
				width: 261px;
				height: 261px;
				padding: 0px;
				border: 0px;
				background: transparent url(<xsl:value-of select="$stylesheetURL"/>/merrills/merrillsboard.png);
			}
			div.merrillsheaps{
				position: relative;
				padding: 10px;
				border: 0px;
			}

		</style>
		
		<div class="merrillsboard">
			<xsl:for-each select="fact[prop-f='PIT']">
				<xsl:variable name="alt"><xsl:call-template name="fluent2text"/></xsl:variable>
				<div>
					<xsl:attribute name="class"><xsl:value-of select="arg[1]"/>_<xsl:value-of select="arg[2]"/></xsl:attribute>
					<xsl:choose>
						<xsl:when test="arg[3]='EMPTY'"/>
						<xsl:otherwise>
							<xsl:call-template name="make_stone">
								<xsl:with-param name="role" select="arg[3]"></xsl:with-param>
								<xsl:with-param name="alt" select="$alt"></xsl:with-param>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</xsl:for-each>
		</div>
		
		<br/>
		
		<div class="merrillsheaps">
			<xsl:for-each select="fact[prop-f='HEAP']">
			<xsl:sort select="arg[1]" order="descending"/>
			<div>
				<xsl:call-template name="makeheap">
					<xsl:with-param name="role"><xsl:value-of select="arg[1]"/></xsl:with-param>
					<xsl:with-param name="size"><xsl:value-of select="arg[2]"/></xsl:with-param>
				</xsl:call-template>
			</div>
			<br/>
			</xsl:for-each>
		</div>
	
	</xsl:template>

	<xsl:template name="makeheap">
		<xsl:param name="role"/>
		<xsl:param name="size"/>
		<xsl:if test="$size!='0'">
			<xsl:call-template name="make_stone">
				<xsl:with-param name="role" select="$role"/>
				<xsl:with-param name="alt" select="$role"/>
			</xsl:call-template>
			<xsl:call-template name="makeheap">
				<xsl:with-param name="role" select="$role"/>
				<xsl:with-param name="size"><xsl:value-of select="$size - 1"/></xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="make_stone">
		<xsl:param name="role"/>
		<xsl:param name="alt"/>
		<img width="26" height="26">
			<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>/merrills/<xsl:value-of select="translate($role,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>stone.gif</xsl:attribute>
			<xsl:attribute name="alt"><xsl:value-of select="$alt"/></xsl:attribute>
			<xsl:attribute name="title"><xsl:value-of select="$alt"/></xsl:attribute>
		</img>
	</xsl:template>

</xsl:stylesheet>
