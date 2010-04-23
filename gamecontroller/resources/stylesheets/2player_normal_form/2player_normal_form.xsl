<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../generic/template.xsl"/>

	<xsl:template name="print_state">
	
		<style type="text/css" media="all">
			#reward {
				border: 1px solid black;
			}
			.action {
				border: 1px solid black;
			}
			.reward {
				border: 1px solid black;
			}
			
		</style>

		<!-- Draw Table -->
		<table id="reward">
			<tr>
			<th class="action"/>
			<xsl:for-each select="/match/state/fact[prop-f='REWARD']">
				<xsl:sort select="arg[2]" order="ascending"/>
				<xsl:if test = "position() = 1 or ( position() > 1 and not(arg[2] = ./preceding::node()/arg[2]))">
					<th class="action">
					<xsl:value-of select="arg[2]"/>
					</th>
				</xsl:if>
			</xsl:for-each>
			</tr>
			<xsl:for-each select="/match/state/fact[prop-f='REWARD']">
				<xsl:sort select="arg[1]" order="ascending"/>
				<xsl:if test = "position() = 1 or ( position() > 1 and not(arg[1] = ./preceding::node()/arg[1]))">
					<xsl:variable name="row_action" select="arg[1]"/>
					<tr>
					<th class="action">
					<xsl:value-of select="$row_action"/>
					</th>
					<xsl:for-each select="/match/state/fact[prop-f='REWARD']">
						<xsl:sort select="arg[2]" order="ascending"/>
						<xsl:if test="arg[1]=$row_action">
							<td class="reward">
							<xsl:if test="/match/state/fact[prop-f='DID' and arg[1]='ROW']/arg[2] = $row_action and 
								/match/state/fact[prop-f='DID' and arg[1]='COLUMN']/arg[2] = arg[2]">
								<xsl:attribute name="style"><xsl:text>background-color: red;</xsl:text></xsl:attribute> 
							</xsl:if>
							<xsl:value-of select="arg[3]"/>, <xsl:value-of select="arg[4]"/></td>
						</xsl:if> 
					</xsl:for-each>
					</tr>
				</xsl:if>
			</xsl:for-each>
		</table>
	
	</xsl:template>

</xsl:stylesheet>
