local arg = table.pack(...)
cols={}
xLen = (arg[1]-1) or 1
yLen = (arg[2]-1) or 1

-- Heres where the generation happens (above is boilerplate)
for a=0,xLen,1 do
	cols[a] = {}
	for b=0,yLen,1 do
		if ( a >= 5 or b >= 5 ) then
			cols[a][b] = math.random(0,3)
		else
			cols[a][b] = math.random(3)
		end
	end
end
-- and everything after this is boilerplate

retS = "["
for a=0,xLen,1 do
	retS = retS .. "["
	for b=0,yLen,1 do
		retS = retS .. cols[a][b]
		if b ~= yLen then
			retS = retS .. ","
		end
	end
	retS = retS .. "]"
	if a ~= xLen then
		retS = retS .. ","
	end
end
retS = retS .. "]"

return retS