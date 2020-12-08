;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot nearestGhostToPacmanDistance (type FLOAT))	
	(slot GhostToNearestEdibleGhostDistance (type FLOAT))

	(slot GhostToNearestActiveGhostDistance (type FLOAT))
)
		
(deftemplate INKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot nearestGhostToPacmanDistance (type FLOAT))	
	(slot GhostToNearestEdibleGhostDistance (type FLOAT))

	(slot GhostToNearestActiveGhostDistance (type FLOAT))
)

(deftemplate PINKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot nearestGhostToPacmanDistance (type FLOAT))	
	(slot GhostToNearestEdibleGhostDistance (type FLOAT))

	(slot GhostToNearestActiveGhostDistance (type FLOAT))
)

(deftemplate SUE
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot nearestGhostToPacmanDistance (type FLOAT))	
	(slot GhostToNearestEdibleGhostDistance (type FLOAT))

	(slot GhostToNearestActiveGhostDistance (type FLOAT))
)

(deftemplate CHECKMATE 
    (slot isCheckMate (type SYMBOL))
)
    
;DEFINITION OF THE ACTION FACT
(deftemplate ACTION
	(slot id) (slot info (default "")) 
) 

(defrule INKYcheckmate
	(INKY (strong true)) (CHECKMATE (isCheckMate true))
	=> 
	(assert (ACTION (id INKYcheckmate) (info "checkmate --> checkmate") )))	

(defrule INKYprotects
	(INKY (strong true)) (INKY (nearestGhostToPacmanDistance ?d)) (test (<= ?d 25)) (INKY (GhostToNearestEdibleGhostDistance ?d)) (test (<= ?d 25)) 
	=> 
	(assert (ACTION (id INKYprotects) (info "puedo proteger aliado --> protejo") ))
)	

(defrule INKYsecure
	(INKY (strong true)) (INKY (canSecurePPill true)))
	=> 
	(assert (ACTION (id INKYsecure) (info "puedo asegurar ppill --> la aseguro") )))	

(defrule INKYchase
	(INKY (strong true))
	=> 
	(assert (ACTION (id INKYchase) (info "no puedo hacer nada fancy --> le persigo") )))	

(defrule INKYseeksProtection
	(INKY (strong false)) (INKY (GhostToNearestActiveGhostDistance ?d)) (test (<= ?d 25))) )
	=>
	(assert (ACTION (id INKYseeksProtection) (info "soy debil y alguien puede protegerme --> me acerco a el") )))

(defrule INKYrunsAway
	(INKY (strong false))
	=>  
	(assert (ACTION (id INKYrunsAway) (info "soy debil --> huyo") )))
	