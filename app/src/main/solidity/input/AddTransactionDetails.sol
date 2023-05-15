pragma solidity ^0.5.0;

contract UserAddressBook {
    mapping(address => address[]) private _userAddresses;
    mapping(address => mapping(address => string)) private _transactionDetails;

    function getUserAddresses() public view returns (address[] memory) {
        return _userAddresses[msg.sender];
    }

    function addUserAddress(address userAddress, string memory userName) public {
        _userAddresses[msg.sender].push(userAddress);
        _userNames[msg.sender][userAddress] = userName;
    }

    function removeUserAddress(address userAddress) public {
        uint userAddressesLength = _userAddresses[msg.sender].length;
        for(uint i = 0; i < userAddressesLength; i++) {
            if (userAddress == _userAddresses[msg.sender][i]) {
                if (1 < _userAddresses[msg.sender].length && i < userAddressesLength-1) {
                    _userAddresses[msg.sender][i] = _userAddresses[msg.sender][userAddressesLength-1];
                }
                delete _userAddresses[msg.sender][userAddressesLength-1];
                _userAddresses[msg.sender].length--;
                delete _userNames[msg.sender][userAddress];
                break;
            }
        }
    }

    function getUserName(address userAddress) public view returns (string memory) {
        return _userNames[msg.sender][userAddress];
    }
}