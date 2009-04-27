<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for writing move history to the screen.
	- For use within <body>.
	- needs css/main.css and sitespecific.xsl
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="history">

		<div class="history">

			<span class="heading">History: </span>
			<div class="underline"/>

			<table>
				<xsl:for-each select="match/history/step">
					<tr>
						<td>
							<span class="heading">
							<a>
								<xsl:attribute name="href">
									<xsl:call-template name="makeStepLinkURL">
										<xsl:with-param name="step" select="./step-number"/>
									</xsl:call-template>
								</xsl:attribute>
								<xsl:value-of select="./step-number"/>.
							</a>
							</span>
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

