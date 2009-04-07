<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for writing state to the screen.
	- For use within <body>.
	- Accepts args: xPos and yPos which specify absolute position.	
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="state">
	
		<xsl:param name="xPos"/>
		<xsl:param name="yPos"/>

		<style type="text/css" media="all">@import 
			url("css/main.css");
		</style>
			
		<div id="state">

			<xsl:attribute name="style">
				position:absolute;
				left: <xsl:value-of select="$xPos"/>;
				top:  <xsl:value-of select="$yPos"/>;
			</xsl:attribute>
			
			<span class="heading">State: </span>
			<div id="underline" style="width:150px"></div>
			
			<table>
				<xsl:for-each select="match/state/fact">
					<tr>
						<td>
							<span class="heading">(<xsl:value-of select="./prop-f"/></span>
								<xsl:for-each select="./arg">
									<xsl:text> </xsl:text>
									<span class="content">
										<xsl:value-of select="."/>
									</span>	
								</xsl:for-each>
							<span class="heading">)</span>
						</td>
					</tr>
				</xsl:for-each>
			</table>	

		</div>

	</xsl:template>
</xsl:stylesheet>	