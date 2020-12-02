;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

	(slot seekProtection (type SYMBOL))
)
		
(deftemplate INKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

	(slot seekProtection (type SYMBOL))
)

(deftemplate PINKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

	(slot seekProtection (type SYMBOL))
)

(deftemplate SUE
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

	(slot seekProtection (type SYMBOL))
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
	(SUE (strong true)) (SUE (canProtectAlly true))
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
	(SUE (strong false)) (SUE (seekProtection true))
	=>
	(assert (ACTION (id SUEseeksProtection) (info "soy debil y alguien puede protegerme --> me acerco a el) )))

(defrule SUErunsAway
	(SUE (strong false))
	=>  
	(assert (ACTION (id SUErunsAway) (info "soy debil --> huyo") )))
	

	