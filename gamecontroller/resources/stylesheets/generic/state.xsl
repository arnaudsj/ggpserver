<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for writing state to the screen.
	- assumes to be in the context /match/state
	- For use within <body>
	- needs css/main.css
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template name="state">

		<span class="heading">State: </span>
		<div class="underline" style="width:150px"></div>

		<table>
			<xsl:for-each select="fact">
				<xsl:sort select="."/>

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

	</xsl:template>

</xsl:stylesheet>
