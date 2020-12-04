(deftemplate MSPACMAN
	(slot numGhostAlive (type INTEGER))
	(slot numPP (type INTEGER)))

(deftemplate ACTION
	(slot id) (slot info (default ""))) 
	
	
(defrule EatPP
	(MSPACMAN (numPP ?d)) (test (> ?d 0)) 
	=>  
	(assert (ACTION (id EatPP))))
	
(defrule EatGhost
	(MSPACMAN (numGhostAlive ?d)) (test (> ?d 0)) 
	=>  
	(assert (ACTION (id EatGhost))))
	
	
