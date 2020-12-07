(deftemplate MSPACMAN
	(slot numPP (type INTEGER))
	(slot numGhostAlive (type INTEGER))
	(slot danger (type SYMBOL)))

(deftemplate ACTION
	(slot id) (slot info (default ""))) 
	
	
(defrule EatGhost
	(MSPACMAN (numGhostAlive ?d)) (test (> ?d 0)) 
	=>  
	(assert (ACTION (id EatGhost))))
	
(defrule Chill
	(MSPACMAN (numGhostAlive ?d)) (test (< ?d 1)) 
	(MSPACMAN (danger false))
	=>
	(assert (ACTION (id Chill))))
	
(defrule EatPowerPill
	(MSPACMAN (numGhostAlive ?d)) (test (< ?d 1)) 
	(MSPACMAN (danger true)) 
	=>
	(assert (ACTION (id EatPowerPill))))
	
(defrule RunAway
	(MSPACMAN (numGhostAlive ?d)) (test (< ?d 1)) 
	(MSPACMAN (danger true))
	(MSPACMAN (numPP  ?d)) (test (< ?d 1))
	=>
	(assert (ACTION (id RunAway))))
