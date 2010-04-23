<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for writing roles and associated players to the screen.
	- For use within <body>.
	- needs css/main.css
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template name="playerInfo">

		<div class="playerInfo">

			<span class="heading">Players: </span>
			<div class="underline"/>

			<table>
				<tr>
					<td>
						<!-- Role names -->
						<table>
							<xsl:for-each select="match/role">
								<tr><td>
									<span class="heading">
										<xsl:variable name="up" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
										<xsl:variable name="lo" select="'abcdefghijklmnopqrstuvwxyz'"/>
										<xsl:choose>
											<!-- hard coded control toggle matching -->
											<xsl:when test="translate(/match/state/fact[contains(prop-f,'CONTROL')]/arg,$up,$lo)=translate(.,$up,$lo)">
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
					</td>

					<!-- Player names -->
					<td>
						<table>
							<xsl:for-each select="match/player">
								<tr><td>
									<span class="content">
										<xsl:value-of select="."/>
									</span>
								</td></tr>
							</xsl:for-each>
						</table>
					</td>

					<!-- Player scores -->
					<td>
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
					</td>
				</tr>
			</table>
		</div>

	</xsl:template>
</xsl:stylesheet>
