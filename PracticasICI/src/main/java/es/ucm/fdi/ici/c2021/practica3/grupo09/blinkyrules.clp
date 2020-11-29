;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot run (type SYMBOL)))
	(slot seekProtection (type SYMBOL))
	(slot canProtectAlly (type SYMBOL))	
	
(deftemplate INKY
	(slot run (type SYMBOL)))
	(slot seekProtection (type SYMBOL))
	(slot canProtectAlly (type SYMBOL))	


(deftemplate PINKY
	(slot run (type SYMBOL)))
	(slot seekProtection (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

(deftemplate SUE
	(slot run (type SYMBOL)))
	(slot seekProtection (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	
	
(deftemplate MSPACMAN 
    (slot mindistancePPill (type NUMBER)) )
    
;DEFINITION OF THE ACTION FACT
(deftemplate ACTION
	(slot id) (slot info (default "")) ) 
   
;RULES 
;(defrule BLINKYrunsAwayMSPACMANclosePPill
;	(MSPACMAN (mindistancePPill ?d)) (test (<= ?d 30)) 
;	=>  
;	(assert (ACTION (id BLINKYrunsAway) (info "MSPacMan cerca PPill"))) )

(defrule BLINKYchases
	(BLINKY (edible false)) 
	=> 
	(assert (ACTION (id BLINKYchases) (info "No comestible --> perseguir") )))	

(defrule BLINKYprotects
	(BLINKY (canProtectAlly true)) 
	=> 
	(assert (ACTION (id BLINKYprotects) (info "Un aliado esta en apuros --> voy a salvarlo") )))	


(defrule BLINKYseeksProtection
	(BLINKY seekProtection true)
	=>
	(assert (ACTION (id BLINKYgoToActive) (info "soy débil y hay alguien que me proteja --> me acerco a él) )))

(defrule BLINKYrunsAway
	(BLINKY (run true)) 
	=>  
	(assert (ACTION (id BLINKYrunsAway) (info "Comestible --> huir") )))
	

	