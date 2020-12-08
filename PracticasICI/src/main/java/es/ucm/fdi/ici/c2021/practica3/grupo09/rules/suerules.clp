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
(defrule SUEcheckmate
	(SUE (strong true)) (CHECKMATE (isCheckMate true))
	=> 
	(assert (ACTION (id SUEcheckmate) (info "checkmate --> checkmate") )))	

(defrule SUEprotects
	(SUE (strong true)) (SUE (nearestGhostToPacmanDistance ?d) (GhostToNearestEdibleGhostDistance ?g)) (test (and (<= ?d 25)(<= ?g 25)(> ?g 0)) ) 
	=> 
	(assert (ACTION (id SUEprotects) (info "puedo proteger aliado --> protejo") )))	

(defrule SUEsecure
	(SUE (strong true)) (SUE (canSecurePPill true))
	=> 
	(assert (ACTION (id SUEsecure) (info "puedo asegurar ppill --> la aseguro") )))	

(defrule SUEchase
	(SUE (strong true))
	=> 
	(assert (ACTION (id SUEchase) (info "no puedo hacer nada fancy --> le persigo") )))	

(defrule SUEseeksProtection
	(SUE (strong false)) (SUE (GhostToNearestActiveGhostDistance ?d)) (test (<= ?d 25))
	=>
	(assert (ACTION (id SUEseeksProtection) (info "soy debil y alguien puede protegerme --> me acerco a el") )))

(defrule SUErunsAway
	(SUE (strong false)) 
	=> 
	(assert (ACTION (id SUErunsAway) (info "soy debil --> huyo") )))	