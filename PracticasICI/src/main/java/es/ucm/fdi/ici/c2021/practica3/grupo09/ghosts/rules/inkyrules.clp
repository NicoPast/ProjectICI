;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot nearestGhostToPacmanDistance (type NUMBER))	
	(slot GhostToNearestEdibleGhostDistance (type NUMBER))

	(slot GhostToNearestActiveGhostDistance (type NUMBER))
)
		
(deftemplate INKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot nearestGhostToPacmanDistance (type NUMBER))	
	(slot GhostToNearestEdibleGhostDistance (type NUMBER))

	(slot GhostToNearestActiveGhostDistance (type NUMBER))
)

(deftemplate PINKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot nearestGhostToPacmanDistance (type NUMBER))	
	(slot GhostToNearestEdibleGhostDistance (type NUMBER))

	(slot GhostToNearestActiveGhostDistance (type NUMBER))
)

(deftemplate SUE
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot nearestGhostToPacmanDistance (type NUMBER))	
	(slot GhostToNearestEdibleGhostDistance (type NUMBER))

	(slot GhostToNearestActiveGhostDistance (type NUMBER))
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
	(INKY (strong true)) (INKY (nearestGhostToPacmanDistance ?d) (GhostToNearestEdibleGhostDistance ?g)) (test (and (<= ?d 25)(<= ?g 25)(> ?g 0)) ) 
	=> 
	(assert (ACTION (id INKYprotects) (info "puedo proteger aliado --> protejo") )))	

(defrule INKYsecure
	(INKY (strong true)) (INKY (canSecurePPill true))
	=> 
	(assert (ACTION (id INKYsecure) (info "puedo asegurar ppill --> la aseguro") )))	

(defrule INKYchase
	(INKY (strong true)) (INKY (canSecurePPill false))
	=> 
	(assert (ACTION (id INKYchase) (info "no puedo hacer nada fancy --> le persigo") )))	

(defrule INKYseeksProtection
	(INKY (strong false)) (INKY (GhostToNearestActiveGhostDistance ?d)) (test (and (> ?d 0)(<= ?d 25)))
	=>
	(assert (ACTION (id INKYseeksProtection) (info "soy debil y alguien puede protegerme --> me acerco a el") )))

(defrule INKYrunsAway
	(INKY (strong false)) 
	=> 
	(assert (ACTION (id INKYrunsAway) (info "soy debil --> huyo") )))	