
; personal infos

(<= (sees_xml ?p (has_dice ?p ?x ?y))
	(true (has_dice ?p ?x ?y))
)

(<= (sees_xml random (has_dice ?p ?x ?y)) ; random must have complete information, included players dices
	(true (has_dice ?p ?x ?y))
)


; phases

(<= (sees_xml ?p (rolling_for ?q))
	(role ?p)
	(true (rolling_for ?q))
)

(<= (sees_xml ?p (claiming ?q))
	(role ?p)
	(true (claiming ?q))
)

(<= (sees_xml ?p (guessing ?q))
	(role ?p)
	(true (guessing ?q))
)

(<= (sees_xml ?p (game_over ?q))
	(role ?p)
	(true (game_over ?q))
)


; general infos

(<= (sees_xml ?p (previous_claimed_values ?x ?y))
	(role ?p)
	(true (previous_claimed_values ?x ?y))
	(distinct ?x 0)
)

