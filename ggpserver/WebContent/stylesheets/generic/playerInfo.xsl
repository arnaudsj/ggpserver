<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for writing roles and associated players to the screen.
	- For use within <body>.
	- Accepts args: xPos and yPos which specify absolute position.	
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="playerInfo">
	
		<xsl:param name="xPos"/>
		<xsl:param name="yPos"/>

		<style type="text/css" media="all">@import 
			url("../stylesheets/generic/css/main.css");
		</style>
			
		<div id="playerInfo">
		
			<xsl:attribute name="style">
				position:absolute;
				left: <xsl:value-of select="$xPos"/>;
				top:  <xsl:value-of select="$yPos"/>;
			</xsl:attribute>
			
			<span class="heading">Players: </span>
			<div id="underline" style="width:150px"></div>
			
			<!-- Role names -->
			<div style="float:left;">
				<table>
					<xsl:for-each select="match/role">
						<tr><td>
							<span class="heading">
								<xsl:variable name="up" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
								<xsl:variable name="lo" select="'abcdefghijklmnopqrstuvwxyz'"/>
								<xsl:choose>
									<!-- hard coded control toggle matching -->
									<xsl:when test="translate(/match/state/fact[prop-f='CONTROL']/arg[1],$up,$lo)=translate(.,$up,$lo)">
										<xsl:value-of select="."/>*
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="."/>
									</xsl:otherwise>
								</xsl:choose>
							</span>		
						</td></tr>	
					</xsl:for-each>
				</table>	
			</div>
			
			<!-- Player names -->
			<div style="float:left;">
				<table>
					<xsl:for-each select="match/player">
						<tr><td>
							<span class="content">
								<xsl:value-of select="."/>
							</span>	
						</td></tr>	
					</xsl:for-each>
				</table>	
			</div>

			<!-- Player scores -->
			<div style="float:left;">
				<table>
					<xsl:for-each select="match/scores/reward">
						<tr><td>
							<span class="heading">
								Score: <xsl:text> </xsl:text>
							</span>
							<span class="content">
								<xsl:value-of select="."/>
							</span>	
						</td></tr>	
					</xsl:for-each>
				</table>	
			</div>
			
		</div>
	
	</xsl:template>
</xsl:stylesheet>	
	
