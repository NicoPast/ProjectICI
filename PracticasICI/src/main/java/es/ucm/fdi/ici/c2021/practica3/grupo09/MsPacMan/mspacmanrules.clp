(deftemplate MSPACMAN 
    (slot numGhostAlive (type NUMBER))
    (slot numPowerPills (type NUMBER)))

(deftemplate ACTION
	(slot id) (slot info (default ""))) 
	
(defrule MsPacManEatGhost
	(MSPACMAN (numGhostAlive ?d)) (test (> ?d 0)) 
	=>  
	(assert (ACTION (id EatGhost) (info "PruebaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))))
	
(defrule MsPacManEatGhost
	(MSPACMAN (numPowerPills ?d)) (test (> ?d 0)) 
	=>  
	(assert (ACTION (id EatPP) (info "PruebaBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB"))))