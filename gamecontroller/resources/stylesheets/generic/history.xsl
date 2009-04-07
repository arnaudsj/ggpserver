<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for writing move history to the screen.
	- For use within <body>.
	- Accepts args: xPos and yPos which specify absolute position.	
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="history">
	
		<xsl:param name="xPos"/>
		<xsl:param name="yPos"/>

		<style type="text/css" media="all">@import 
			url("../../stylesheets/generic/css/main.css");
		</style>
			
		<div id="history">
		
			<xsl:attribute name="style">
				position:absolute;
				left: <xsl:value-of select="$xPos"/>;
				top:  <xsl:value-of select="$yPos"/>;
			</xsl:attribute>
			
			<span class="heading">History: </span>
			<div id="underline" style="width:150px"></div>
			
			<table>
				<xsl:for-each select="match/history/step">
					<tr>
						<td>
							<span class="heading"><xsl:value-of select="./step-number"/>.</span>
						</td>
						<xsl:for-each select="./move">
							<td>
								<span class="content"><xsl:value-of select="."/></span>
							</td>
						</xsl:for-each>
					</tr>
				</xsl:for-each>
			</table>	
			
		</div>

	</xsl:template>
</xsl:stylesheet>	
	
	