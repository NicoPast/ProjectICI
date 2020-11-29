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
    (slot mindistancePPill))
    
(deftemplate ACTION
	(slot id))   
    
(defrule INKYchases
	(INKY (run false)) => (assert (ACTION (id INKYchases))))

(defrule INKYprotects
	(INKY (canProtectAlly true)) 
	=> 
	(assert (ACTION (id INKYprotects) (info "Un aliado esta en apuros --> voy a salvarlo") )))	


(defrule INKYseeksProtection
	(INKY seekProtection true)
	=>
	(assert (ACTION (id INKYgoToActive) (info "soy débil y hay alguien que me proteja --> me acerco a él) )))

(defrule INKYrunsAway
	(INKY (run true)) =>  (assert (ACTION (id INKYrunsAway))))


	
	