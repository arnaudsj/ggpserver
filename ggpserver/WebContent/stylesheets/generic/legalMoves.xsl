<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
	- Widget for writing legalMoves for a HumanPlayer to the screen.
	- For use within <body>.
	- needs css/main.css and sitespecific.xsl
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:template name="loadingImg">
		<img width="16" height="16" title="waiting for opponent...">
			<xsl:attribute name="src"><xsl:value-of select="$stylesheetURL"/>/generic/images/16-loading.gif</xsl:attribute>
			loading...
		</img>
	</xsl:template>
	
	
	<xsl:template name="legalMoves">
		
		<xsl:variable name="playing">
			<xsl:choose>
				<xsl:when test="count(/match/legalmoves) = 1">1</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="currentStep" select="count(/match/history/step)+1"/>
		<xsl:variable name="role" select="/match/sight-of"/>
				
		<xsl:choose>
		<xsl:when test="$playing = 1"> <!-- display something only if we are playing-->
			<div class="legalMoves">
				
				<span class="heading">Legal moves: </span>
				<div class="underline"/>
				
				<input type="checkbox" name="autoConfirm" value="auto">
					<xsl:if test="/match/quickConfirm">
						<xsl:attribute name="checked" />
					</xsl:if>
					<xsl:attribute name="onclick">
						javascript:location.replace("<xsl:call-template name="makePlayLinkURL">
								<xsl:with-param name="role" select="$role"/>
								<xsl:with-param name="chosenMove" select="-1"/>
							</xsl:call-template>");
					</xsl:attribute>
				</input>
				<span title="When this box is checked, you will not be prompted when you have only one possible move, it will be automatically confirmed. This can be more pleasant if you have to play 'NOOPs' very often.">Quick confirm (?)</span>
				
				<style type="text/css" media="all">
					td:last-child {padding-right: 20px;} /*prevent Mozilla scrollbar from hiding cell content*/
				</style>
				
				<div style="overflow: auto; max-height: 420px; overflow-x: hidden;">
					<table>
						<tbody>
							<xsl:choose>
								<xsl:when test="$currentStep = 1 and count(match/ready) = 0">
									<span class="content">
									<a>
										<xsl:attribute name="href">
											javascript:location.replace("
											<xsl:call-template name="makePlayLinkURL">
												<xsl:with-param name="role" select="$role"/>
												<xsl:with-param name="chosenMove" select="-2"/>
											</xsl:call-template>
											")
										</xsl:attribute>
										I'm ready!
									</a>
									</span>
								</xsl:when>
								<xsl:when test="count(/match/chosenmove/confirmed) = 1"> <!-- reload the page in case the move has been confirmed... -->
									- move already confirmed (<xsl:value-of select="/match/chosenmove" />) -<br/>
									<xsl:call-template name="loadingImg" />
									<script language="JavaScript" type="text/javascript">
										<![CDATA[
											setTimeout("document.location.reload();", 2500);
										]]>
									</script>
								</xsl:when>
								<xsl:when test="count(/match/legalmoves/move) = 0"> <!-- reload the page if we don't have the legalMoves yet -->
									<xsl:call-template name="loadingImg" />
									<script language="JavaScript" type="text/javascript">
										<![CDATA[
											setTimeout("document.location.reload();", 2500);
										]]>
									</script>
								</xsl:when>
								<xsl:when test="/match/quickConfirm and count(/match/legalmoves/move) = 1"> <!-- confirm automatically if there is only one possible move, and if the player wants that -->
									<script language="JavaScript" type="text/javascript">
											javascript:location.replace("<xsl:call-template name="makePlayLinkURL">
													<xsl:with-param name="role" select="$role"/>
													<xsl:with-param name="forStepNumber" select="count(/match/history/step)+1"/>
													<xsl:with-param name="confirm" select="1"/>
												</xsl:call-template>");
									</script>
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="count(/match/legalmoves/move) != 0">
										<tr>
											<td>
												<span class="content">
												<a>
													<xsl:attribute name="href">
														javascript:location.replace("
														<xsl:call-template name="makePlayLinkURL">
															<xsl:with-param name="role" select="$role"/>
															<xsl:with-param name="forStepNumber" select="count(/match/history/step)+1"/>
															<xsl:with-param name="confirm" select="1"/>
														</xsl:call-template>
														")
													</xsl:attribute>
													Confirm move "<xsl:value-of select="/match/chosenmove" />"
												</a>
												</span>
											</td>
										</tr>
									</xsl:if>
									<xsl:for-each select="/match/legalmoves/move">
										<xsl:sort select="move-term"/>
										<xsl:variable name="url">
											<xsl:call-template name="makePlayLinkURL">
												<xsl:with-param name="role" select="$role"/>
												<xsl:with-param name="forStepNumber" select="count(/match/history/step)+1"/>
												<xsl:with-param name="chosenMove" select="./move-number"/>
											</xsl:call-template>
										</xsl:variable>
										<xsl:variable name="urlWithConfirm">
											<xsl:call-template name="makePlayLinkURL">
												<xsl:with-param name="role" select="$role"/>
												<xsl:with-param name="forStepNumber" select="count(/match/history/step)+1"/>
												<xsl:with-param name="chosenMove" select="./move-number"/>
												<xsl:with-param name="confirm" select="1"/>
											</xsl:call-template>
										</xsl:variable>
										<xsl:call-template name="legalMove">
											<xsl:with-param name="url" select="$url" />
											<xsl:with-param name="urlWithConfirm" select="$urlWithConfirm" />
										</xsl:call-template>
									</xsl:for-each>
									<tr></tr>
								</xsl:otherwise>
							</xsl:choose>
						</tbody>
					</table>
				</div>
	
			</div>
		</xsl:when>
		</xsl:choose>

	</xsl:template>
</xsl:stylesheet>

