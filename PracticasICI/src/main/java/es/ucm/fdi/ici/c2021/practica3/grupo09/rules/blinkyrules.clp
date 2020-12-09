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
    (slot isCheckMate  (type SYMBOL)))
    
;DEFINITION OF THE ACTION FACT
(deftemplate ACTION
	(slot id) (slot info (default "")) ) 

(defrule BLINKYcheckmate
	(BLINKY (strong true)) (CHECKMATE (isCheckMate true))
	=> 
	(assert (ACTION (id BLINKYcheckmate) (info "checkmate --> checkmate") )))	

(defrule BLINKYprotects
	(BLINKY (strong true)) (BLINKY (nearestGhostToPacmanDistance ?d) (GhostToNearestEdibleGhostDistance ?g)) (test (and (<= ?d 25)(<= ?g 25)(> ?g 0)) ) 
	=> 
	(assert (ACTION (id BLINKYprotects) (info "puedo proteger aliado --> protejo") )))	

(defrule BLINKYsecure
	(BLINKY (strong true)) (BLINKY (canSecurePPill true))
	=> 
	(assert (ACTION (id BLINKYsecure) (info "puedo asegurar ppill --> la aseguro") )))	

(defrule BLINKYchase
	(BLINKY (strong true)) (BLINKY (canSecurePPill false))
	=> 
	(assert (ACTION (id BLINKYchase) (info "no puedo hacer nada fancy --> le persigo") )))	

(defrule BLINKYseeksProtection
	(BLINKY (strong false)) (BLINKY (GhostToNearestActiveGhostDistance ?d)) (test (<= ?d 25))
	=>
	(assert (ACTION (id BLINKYseeksProtection) (info "soy debil y alguien puede protegerme --> me acerco a el") )))

(defrule BLINKYrunsAway
	(BLINKY (strong false)) 
	=> 
	(assert (ACTION (id BLINKYrunsAway) (info "soy debil --> huyo") )))	