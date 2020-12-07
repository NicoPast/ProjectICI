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

(defrule SUEcheckmate
	(SUE (strong true)) (CHECKMATE (isCheckMate true))
	=> 
	(assert (ACTION (id SUEcheckmate) (info "checkmate --> checkmate") )))	

(defrule SUEprotects
	(SUE (strong true)) (SUE (nearestGhostToPacmanDistance ?d)) (test (<= ?d 25)) (SUE (GhostToNearestEdibleGhostDistance ?d)) (test (<= ?d 25)) 
	=> 
	(assert (ACTION (id SUEprotects) (info "puedo proteger aliado --> protejo") ))
)	

(defrule SUEsecure
	(SUE (strong true)) (SUE (canSecurePPill true)))
	=> 
	(assert (ACTION (id SUEsecure) (info "puedo asegurar ppill --> la aseguro") )))	

(defrule SUEchase
	(SUE (strong true))
	=> 
	(assert (ACTION (id SUEchase) (info "no puedo hacer nada fancy --> le persigo") )))	

(defrule SUEseeksProtection
	(SUE (strong false)) (SUE (GhostToNearestActiveGhostDistance ?d)) (test (<= ?d 25))) )
	=>
	(assert (ACTION (id SUEseeksProtection) (info "soy debil y alguien puede protegerme --> me acerco a el") )))

(defrule SUErunsAway
	(SUE (strong false))
	=>  
	(assert (ACTION (id SUErunsAway) (info "soy debil --> huyo") )))
	

	